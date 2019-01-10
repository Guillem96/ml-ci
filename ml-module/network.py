from git import Repo

class Network(object):
    
    @staticmethod
    def clone_github_repository(url, out_path):
        """Force clone a github repository
        
        Arguments:
            url {string} -- Github repository https clone link
            out_path {string} -- Path where repo will be stored
        
        Returns:
            string -- Directory where repo has been cloned
        """
        name = url.split('/')[-1]
        directory = os.path.join('cloned', name)

        # In case repository has already been downloaded, remove it
        delete_dir(out_path)
        
        # Clone
        Repo.clone_from(url, directory)
        return directory