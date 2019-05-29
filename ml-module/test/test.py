import sys
from pathlib import Path
import yaml

sys.path.append('.')
from ml_ci.cfg import cfg_parser
from ml_ci.project_manage import ProjectGenerator, ProjectRunner

cloned = Path('test', 'resources', 'iris-clf-project')

with cloned.joinpath('ml-ci.yml').open() as f:
    parsed = cfg_parser.YamlCfgParser(f).parse()

project = ProjectGenerator(parsed, cloned).generate()
runner = ProjectRunner(project, parsed)
runner.run_and_evaluate()