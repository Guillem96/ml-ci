#!/usr/bin/env python

import logging
from pathlib import Path

from ml_ci.cfg.cfg_parser import YamlCfgParser

from ml_ci.project_manage import ProjectGenerator
from ml_ci.project_manage import ProjectRunner

from ml_ci.network import Network
from ml_ci.webservice_logger import RequestsHandler

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
    
    if not cfg_file_path.exists():
        return None

    with cfg_file_path.open() as f:
        cfg = YamlCfgParser(f).parse()
    return cfg


def clean_up_dirs(*dirs):
    for d in dirs:
        delete_dir(str(d))
        Path(d).rmdir()


def run_before_commands(commands, out_path, logger):
    for cmd in commands:
        logger.info('Running command: ' + cmd)
        stdout, stderr = execute(cmd, out_path)
        if stdout:
            logger.debug(stdout)
        if stderr:
            logger.warning(stderr)


def init(repo_id):
    logging.basicConfig(format='[%(levelname)s] %(name)s - %(message)s')
                    
    webservice = Network(tracked_repository=repo_id)
    webservice and webservice.authenticate()
    
    logger = logging.getLogger('MlCi')
    logger.setLevel(logging.DEBUG)
    logger.handlers = []
    logger.addHandler(RequestsHandler(webservice))

    return webservice, logger


def train_step(webservice, output_path, cfg, logger):
    try:
        # Run before scripts
        if cfg.before:
            logger.info('Running before commands...')
            run_before_commands(cfg.before, output_path, logger)

        # Generate DriftAI project
        cfg.project_name += '-{}'.format(webservice and webservice.build_num)
        proj = ProjectGenerator(cfg, output_path, webservice).generate()

        # Run the project lifecycle
        ProjectRunner(proj, cfg, webservice).run_and_evaluate()

        # Remove the project and the cloned repo
        logger.info('Cleaning up environment...')

        webservice and webservice.update_repository_status("TRAINED")

    except Exception as e:
        logger.error('Unexpected error occured while training.\n' + str(e))
        webservice and webservice.update_repository_status("ERROR")


def train(repo_id, url):

    webservice, logger = init(repo_id)
    
    # Update the batch count for this repository
    webservice and webservice.increment_build()
    webservice and webservice.update_repository_status("TRAINING")

    logger.info('Job running...')

    # Clone the repository
    logger.info('Cloning github repository from: {}'.format(url))
    output_path = Network.clone_github_repository(url)
    logger.info('Clone done')
    
    # Parse the config file
    logger.info('Parsing ml-ci.yml file...')

    try:
        cfg = parse_cfg(Path(output_path, 'ml-ci.yml'))
    except Exception as e:
        logger.error('Error parsing file\n' + str(e))
        webservice.update_repository_status("ERROR")
        return

    if cfg:
        logger.debug('File correctly parsed. Go on with the execution')
        train_step(webservice, 
                   output_path, 
                   cfg, 
                   logger)
        clean_up_dirs(output_path)
    else:
        logger.error('Config file not found. Pleas check your repository content')
        webservice.update_repository_status("ERROR")