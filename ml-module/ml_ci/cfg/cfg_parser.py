#!/usr/bin/env python
from functools import partial

import yaml

import optapp as opt

class ModelCfg(object):

    def __init__(self, name, params, fine_tune):
        self.id = None
        self.name = name
        self.params = params
        self.fine_tune = fine_tune

    def __str__(self):
        return '{ ' + 'Algorithm: {}, Params: {}, Fine Tune: {}' \
                            .format(self.name, self.params, self.fine_tune) + ' }'

class MlCiCfg(object):

    def __init__(self, **kwargs):
        self.project_name = kwargs.get('project_name')
        self.datasets = kwargs.get('datasets')
        self.subdatasets = kwargs.get('subdatasets')
        self.approaches = kwargs.get('approaches')


class YamlCfgParser(object):

    def __init__(self, yaml_stream):
        self._file_content = yaml.load(yaml_stream)

    def parse(self):
        config = dict()
        config['project_name'] = self._file_content['project']
        config['datasets'] = self._parse_datasets(self._file_content['datasets'])
        config['subdatasets'] = self._parse_subdatasets(self._file_content['subdatasets'])
        config['approaches'] = self._parse_approaches(self._file_content['approaches'])

        return MlCiCfg(**config)

    def _parse_datasets(self, datasets):

        result = []
        required_fields = ['path', 'id']
        for d in datasets:
            self._check_required_fields(required_fields, d, 'Dataset')
            current_dataset = dict()
            current_dataset['custom'] = d.get('custom', False)
            current_dataset['path'] = d['path']

            if 'dtype' not in d:
                # FileDatasource
                current_dataset['label'] = d.get('label')
                current_dataset['first-line-heading'] = d.get('first-line-heading', True)
                current_dataset['id'] = d['id']
                current_dataset['factory'] = partial(opt.data.Dataset.read_file,
                                                     label=d.get('label'), 
                                                     first_line_heading=current_dataset['first-line-heading'])
            else:
                # Directory or custom datasource
                current_dataset['parsing-pattern'] = d.get('parsing-pattern')
                current_dataset['dtype'] = d['dtype']
                current_dataset['id'] = d['id']
                current_dataset['factory'] = partial(opt.data.Dataset.from_dir, 
                                                     path_pattern=d.get('parsing-pattern'), 
                                                     datatype=d['dtype'])
            result.append(current_dataset)
        return result

    def _parse_subdatasets(self, subdatasets):
        result = []
        required_fields = ['from', 'method']
        for sbds in subdatasets:
            self._check_required_fields(required_fields, sbds, 'Subdataset')
            current_sbds = {
                'from': sbds['from'],
                'id': sbds.get('id'),
                'method': sbds['method']
            }
            if sbds['method'] == 'k_fold':
                if 'k' not in sbds:
                    raise Exception('k field if mandatory when k_fold method is selected')
                current_sbds['by'] = int(sbds['k'])
            elif sbds['method'] == 'train_test':
                if 'train_size' not in sbds:
                    raise Exception('train_size field if mandatory when train_test method is selected')
                current_sbds['by'] = float(sbds['train_size'])
            else:
                raise Exception('Method {} not supported'.format(sbds['method']))
            current_sbds['dataset_factory'] = partial(opt.data.Dataset.load, 
                                                      current_sbds['from'])
            current_sbds['factory'] = partial(opt.data.SubDataset, 
                                              id=current_sbds['id'],
                                              method=current_sbds['method'], 
                                              by=current_sbds['by'])
            result.append(current_sbds)
        return result

    def _parse_approaches(self, approaches):
        result = []
        required_fields = ['name', 'subdataset']
        for a in approaches:
            self._check_required_fields(required_fields, a, 'Approach')
            current_approach = dict(**a)
            current_approach['path'] = a.get('path', '.')
            current_approach['sbds_factory'] = partial(opt.data.SubDataset.load,
                                                       a['subdataset'])
            current_approach['factory'] = partial(opt.Approach, 
                                                  name=a['name'])
            result.append(current_approach)
        return result

    def _check_required_fields(self, required_fields, d, object_name):
        for f in required_fields:
            if f not in d:
                raise Exception('Missing required field \'{}\' from {} object.'
                                .format(f, object_name))
