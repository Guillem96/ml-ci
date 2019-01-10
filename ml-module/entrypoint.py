import os
from cfg_parser.cfg_parser import YamlParser
from trainer import training_stage


def parse_cfg(cfg_file_path):
    """Parse the given ml-ci.yml
    
    Arguments:
        cfg_file_path {string} -- ml-ci.yml path
    
    Returns:
        MlCiCfg -- ML CI configuration
    """
    cfg = None
    with open(cfg_file_path, 'r') as f:
        cfg = YamlParser(f).parsed_cfg
    return cfg


if __name__ == "__main__":
    output_path = Network.clone_github_repository("https://github.com/Guillem96/ml-ci-test.git")
    
    # Parse the config file
    cfg = parse_cfg(os.path.join(output_path, 'ml-ci.yml'))

    if cfg:
        # Start training stage
        cfg.data_set = os.path.join(output_path, cfg.data_set)
        training_stage(cfg)
    else:
        # TODO: Communicate the error to the webservice
        print("No config file provided")