#!/usr/bin/env python

from pathlib import Path

from ml_ci.cfg.cfg_parser import YamlCfgParser

from ml_ci.project_manage import ProjectGenerator
from ml_ci.project_manage import ProjectRunner

from ml_ci.network import Network

from ml_ci.utils import delete_dir

def parse_cfg(cfg_file_path):
    """Parse the given ml-ci.yml
    
    Arguments:
        cfg_file_path {pathlib.Path} -- ml-ci.yml path
    
    Returns:
        MlCiCfg -- ML CI configuration
    """
    cfg = None
    with cfg_file_path.open() as f:
        cfg = YamlCfgParser(f).parse()
    return cfg

def clean_up_dirs(*dirs):
    for d in dirs:
        delete_dir(str(d))
        Path(d).rmdir()
    
def train(repo_id, url):
    webservice = Network(tracked_repository=repo_id)
    webservice.authenticate()
    
    # Update the batch count for this repository
    webservice and webservice.increment_build()
        
    # Clone the repository
    output_path = Network.clone_github_repository(url)
    
    # Parse the config file
    cfg = parse_cfg(Path(output_path, 'ml-ci.yml'))

    if cfg:
        # Start training stage
        proj = ProjectGenerator(cfg, output_path, webservice).generate()
        ProjectRunner(proj, cfg, webservice).run_and_evaluate()

        # Remove the project and the cloned repo
        clean_up_dirs(proj.path, output_path)
    else:
        # TODO: Communicate the error to the webservice
        print("No config file provided")
    