#!/usr/bin/env python

import os
from cfg.cfg_parser import YamlCfgParser
from trainer.trainer import training_stage
from network import Network

def parse_cfg(cfg_file_path):
    """Parse the given ml-ci.yml
    
    Arguments:
        cfg_file_path {string} -- ml-ci.yml path
    
    Returns:
        MlCiCfg -- ML CI configuration
    """
    cfg = None
    with open(cfg_file_path, 'r') as f:
        cfg = YamlCfgParser(f).parse()
    return cfg

def train(repo_id, url):
    webservice = Network(tracked_repository=repo_id)
    output_path = Network.clone_github_repository(url)
    
    # Parse the config file
    cfg = parse_cfg(os.path.join(output_path, 'ml-ci.yml'))

    if cfg:
        # Start training stage
        cfg.data_set = os.path.join(output_path, cfg.data_set)
        training_stage(cfg, webservice)
    else:
        # TODO: Communicate the error to the webservice
        print("No config file provided")
    