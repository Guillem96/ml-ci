import yaml

class ModelCfg(object):

    def __init__(self, name, params, fine_tune):
        self.name = name
        self.params = params
        self.fine_tune = fine_tune

    def __str__(self):
        return '{ ' + 'Algorithm: {}, Params: {}, Fine Tune: {}' \
                            .format(self.name, self.params, self.fine_tune) + ' }'

class MlCiCfg(object):

    def __init__(self, **kwargs):
        self.test_rate = kwargs.get('test_rate')
        self.models = kwargs.get('models')
        self.pipeline = kwargs.get('pipeline')
        self.target = kwargs.get('target')
        self.data_set = kwargs.get('data_set')

        # TODO: Validate mandatory data

    def __str__(self):
        return 'Data-set: {}\nTarget: {}\nTest-rate: {}\nPipeline: {}\nModels: {}' \
                        .format(self.data_set, self.target, self.test_rate, self.pipeline, self._models_str())

    def _models_str(self):
        return '[\n  ' + ',\n  '.join(map(str, self.models)) + '\n]'


class YamlParser(object):

    def __init__(self, yaml_stream):
        cfg_dict = yaml.load(yaml_stream)
        config = dict()
        config['test_rate'] = cfg_dict.get('test-rate', 0.2)
        config['pipeline'] = cfg_dict.get('pipeline')
        config['models'] = YamlParser._parse_models(cfg_dict)
        config['target'] = cfg_dict.get('target')
        config['data_set'] = cfg_dict.get('data-set', 'dataset/dataset.csv')

        self.parsed_cfg = MlCiCfg(**config)


    @staticmethod
    def _parse_models(cfg_dict):
        models = cfg_dict.get('models', [])
        return [ ModelCfg(model, models.get('params'), models.get('fine-tune', False)) 
                            for model in models.keys() ]



if __name__ == '__main__':
    with open('ml-ci.yml', 'r') as f:
        print(str(YamlParser(f).parsed_cfg))