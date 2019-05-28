#!/usr/bin/env python

from pathlib import Path

from ml_ci.cfg.cfg_parser import YamlCfgParser

from ml_ci.project_manage import ProjectGenerator
from ml_ci.project_manage import ProjectRunner

from ml_ci.network import Network

from ml_ci.utils import delete_dir
from ml_ci.utils import execute


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


def run_before_commands(commands, out_path):
    for cmd in commands:
        print(execute(cmd, out_path))


def train(repo_id, url):
    webservice = None #Network(tracked_repository=repo_id)
    webservice and webservice.authenticate()
    
    # Update the batch count for this repository
    webservice and webservice.increment_build()
    webservice and webservice.update_repository_status("TRAINING")

    # Clone the repository
    output_path = Network.clone_github_repository(url)
    
    # Parse the config file
    cfg = parse_cfg(Path(output_path, 'ml-ci.yml'))

    if cfg:
        # Run before scripts
        run_before_commands(cfg.before, output_path)

        # Generate DriftAI project
        cfg.project_name += '-{}'.format(webservice.build_num)
        proj = ProjectGenerator(cfg, output_path, webservice).generate()

        # Run the project lifecycle
        ProjectRunner(proj, cfg, webservice).run_and_evaluate()

        # Remove the project and the cloned repo
        # clean_up_dirs(proj.path, output_path)

        webservice and webservice.update_repository_status("TRAINED")
    else:
        webservice.update_repository_status("ERROR")

        # TODO: Communicate the error to the webservice
        print("No config file provided")
    