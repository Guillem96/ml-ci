#!/usr/bin/env python

from pathlib import Path

from ml_ci.cfg.cfg_parser import YamlCfgParser

from ml_ci.project_manage import ProjectGenerator
from ml_ci.project_manage import ProjectRunner

from ml_ci.network import Network

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

def train(repo_id, url):
    # webservice = Network(tracked_repository=repo_id)
    output_path = Network.clone_github_repository(url)
    
    # Parse the config file
    cfg = parse_cfg(Path(output_path, 'ml-ci.yml'))

    if cfg:
        # Start training stage
        proj = ProjectGenerator(cfg, output_path).generate()
        ProjectRunner(proj, cfg).run_and_evaluate()
    else:
        # TODO: Communicate the error to the webservice
        print("No config file provided")
    