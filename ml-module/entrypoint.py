import os
from git import Repo
from cfg_parser.cfg_parser import YamlParser
from trainer import training_stage

def delete_dir(path):
    for root, dirs, files in os.walk(path, topdown=False):
        for name in files:
            os.chmod(os.path.join(root, name), 0o777)
            os.remove(os.path.join(root, name))
        for name in dirs:
            os.rmdir(os.path.join(root, name))
            
def clone_repository(url):
    name = url.split('/')[-1]
    directory = os.path.join('cloned', name)
    delete_dir(directory)
    
    Repo.clone_from(url, directory)
    return directory

def parse_cfg(cfg_file_path):
    cfg = None
    with open(cfg_file_path, 'r') as f:
        cfg = YamlParser(f).parsed_cfg
    return cfg

if __name__ == "__main__":
    output_path = clone_repository("https://github.com/Guillem96/ml-ci-test.git")
    cfg = parse_cfg(os.path.join(output_path, 'ml-ci.yml'))
    
    # Path relative to cloned dir
    cfg.data_set = os.path.join(output_path, cfg.data_set)
    training_stage(cfg)