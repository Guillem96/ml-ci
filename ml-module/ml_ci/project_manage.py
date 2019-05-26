import sys
import shutil
from pathlib import Path
from inspect import getabsfile

import optapp as opt

class ProjectGenerator(object):
    PROJECTS_PATH = Path('optapp-projects')

    def __init__(self, cfg, src_files=None, webservice=None):
        self.cfg = cfg
        self.src_files = Path(src_files)
        self.webservice = webservice
        self.PROJECTS_PATH.mkdir(exist_ok=True, parents=True)
    
    def _add_custom_datasource(self, project, d):
        sys.path.append(str(self.src_files))

        split_module = d['dtype'].split('.')
        datasource_module = opt.utils.import_from('.'.join(split_module[:-1]), 
                                                 split_module[-1])

        src_datasource = Path(getabsfile(datasource_module))
        dst_datasource = Path(project.path, *split_module[:-2])
        dst_datasource.mkdir(exist_ok=True, parents=True)

        shutil.copy(str(src_datasource), str(dst_datasource.joinpath(src_datasource.name)))

        sys.path.remove(str(self.src_files))

    def generate(self):
        # Generate the project
        project = opt.Project(name=self.cfg.project_name, 
                              path=str(self.PROJECTS_PATH))
        opt.set_project_path(project.path)

        # Add the datasets to project
        for d in self.cfg.datasets:
            if d['custom']:
                self._add_custom_datasource(project, d)
                sys.path.append(project.path)

            if not Path(d['path']).is_absolute():
                d['path'] = str(self.src_files.joinpath(d['path']))

            ds = d['factory'](path=d['path'])
            ds.save()

        # Generate the subdatasets
        for sbds in self.cfg.subdatasets:
            dataset = sbds['dataset_factory']()
            subdataset = sbds['factory'](dataset=dataset)
            subdataset.save()
        
        # Generate approaches
        for a in self.cfg.approaches:
            subdataset = a['sbds_factory']()
            approach = a['factory'](subdataset=subdataset, project=project)
            approach.save()

            self.webservice and self.webservice.create_approach(a)

            src_approach = self.src_files.joinpath(a['path'], a['name'] + '.py')
            shutil.copy(src_approach, approach.script_path)
        
        return project


class ProjectRunner(object):

    def __init__(self, project, cfg, webservice=None):
        self.project = project
        self.cfg = cfg
        self.webservice = webservice

    def _run_approach(self, approach):
        namespace = 'approaches.' + approach['name']
        cls_name = opt.utils.to_camel_case(approach['name']) + "Approach"
        approach_cls = opt.utils.import_from(namespace, cls_name)
        approach_cls().run()

    def _evaluate_approach(self, approach):
        metrics_fns = [opt.result_report.str_to_metric_fn[m] for m in approach['metrics']]
        r = opt.result_report.ResultReport(approach=opt.Approach.load(approach['name']), 
                                            metrics=metrics_fns)

        results_path = Path(self.project.path, 'reports')
        results_path.mkdir(exist_ok=True, parents=True)
        results_path = results_path.joinpath(approach['name'] + "_evaluation.csv")
        
        df = r.as_dataframe()
        df.to_csv(results_path, index=False) 

        return df

    def run_and_evaluate(self):
        opt.set_project_path(self.project.path)
        
        sys.path.append(self.project.path)

        for a in self.cfg.approaches:
            self.webservice and self.webservice.update_approach_status(a, "TRAINING")

            # Run and evaluate the approach
            self._run_approach(a)
            evaluations_df = self._evaluate_approach(a)
            
            # Upload the results
            self.webservice and self.webservice.upload_evaluations(evaluations_df, a)

            # Set approach done
            self.webservice and self.webservice.update_approach_status(a, "TRAINED")

        sys.path.remove(self.project.path)
