The goal is to analyze graph using a cloud computing service - Microsoft Azure, and our task is to write a MapReduce program to compute the distribution of a graph’s node degree differences (see example below). 

Each file stores a list of edges as tab-separated-values. Each line represents a single edge consisting of two columns: (Source, Target), each of which is separated by a tab. Node IDs are positive integers and the rows are already sorted by Source.


Source        Target

1                2

2                 1

2                 3

3                 2

4                 2

4                 3


our code accept two arguments upon running. The first argument (args[0]) will be a path for the input graph file, and the second argument (args[1]) will be a path for output directory. The default output mechanism of Hadoop will create multiple files on the output directory such as part-00000, part-00001, which will have to be merged and downloaded to a local directory 

The format of the output should be as follows. Each line of your output is of the format

diff        count

where

(1) diff is the difference between a node’s out-degree and in-degree (i.e., out-degree minus in-degree); and

(2) count is the number of nodes that have the value of difference (specified in 1).


The out-degree of a node is the number of edges where that node is the Source. The in-degree of a node is the number of edges where that node is the Target. diff and count must be separated by a tab (\t), and lines do not have to be sorted.


The following result is computed based on the toy graph above.



Creating Clusters in HDInsight using the Azure portal

Azure HDInsight is an Apache Hadoop distribution. This means that it handles large amount of data on demand. The next step is to use Azure’s web-based management tool to create a Linux cluster. Follow the documentation here to create a new cluster - make sure to use the following settings:

Select “Quick Create” instead of “Custom”
“Subscription” drop down menu: there will be only 1 option (“<Lab_name>+<your_name>”)
“Cluster type”: choose “Hadoop 2.7.3 (HDI 3.6)”
Under the Storage Tab (Step 3 during cluster creation), under the “Select a Storage Account” textbar, select the option “Create New” to create a new storage account. Preferably name your storage account with lowercase letters

At the end of this process, you will have created and provisioned a New HDInsight Cluster and Storage (the provisioning will take some time depending on how many nodes you chose to create). Please record the following important information (we also recommend that you take screenshots) so you can refer to them later:

Cluster login credentials
SSH credentials
Resource group
Storage account
Container credentials


Uploading data files to HDFS-compatible Azure Blob storage

We have listed the main steps from the documentation for uploading data files to your Azure Blob storage here:


Follow the documentation here to install Azure CLI.
Open a command prompt, bash, or other shell, and use az login command to authenticate to your Azure subscription. When prompted, enter the username and password for your subscription.
az storage account list command will list the storage accounts for your subscription.
az storage account keys list --account-name <storage-account-name> --resource-group <resource-group-name> command should return Primary and Secondary keys. Copy the Primary key value because it will be used in the next steps.
az storage container list --account-name <storage-account-name> --account-key <primary-key-value> command will list your blob containers.
az storage blob upload --account-name <storage-account-name> --account-key <primary-key-value> --file <small or large .tsv> --container-name <container-name> --name <name for the new blob>/<small or large .tsv> command will upload the source file to your blob storage container.

Using these steps, upload small.tsv and large.tsv to your blob storage container. The uploading process may take some time. After that, you can find the uploaded files in storage blobs at Azure (portal.azure.com) by clicking on “Storage accounts” in the left side menu and navigating through your storage account (<Your Storage Account> -> <”Blobs” in the overview tab> -> <Select your Blob container to which you’ve uploaded the dataset> -> <Select the relevant blob folder>). For example, “jonDoeStorage” -> “Blobs” -> “jondoecluster-xxx” -> “jdoeSmallBlob” -> “small.tsv”. After that write your hadoop code locally and convert it to a jar file using the steps mentioned above.



Uploading your Jar file to HDFS-compatible Azure Blob storage

Azure Blob storage is a general-purpose storage solution that integrates with HDInsight. Your Hadoop code should directly access files on the Azure Blob storage.


Upload the jar file created in the first step to Azure storage using the following command:


scp <your-relative-path>/q4-1.0.jar <ssh-username>@<cluster-name>-ssh.azurehdinsight.net:


SSH into the HDInsight cluster using the following command:


ssh <ssh-username>@<cluster-name>-ssh.azurehdinsight.net


Note: if you see the warning - REMOTE HOST IDENTIFICATION HAS CHANGED, you may clean /home/<user>/.ssh/known_hosts”. Please refer to host identification.

 

Run the ls command to make sure that the q4-1.0.jar file is present.


To run your code on the small.tsv file, run the following command:


yarn jar q4-1.0.jar edu.gatech.cse6242.Q4 wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/<name for the new blob>/small.tsv

wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput


Command format: yarn jar jarFile packageName.ClassName dataFileLocation outputDirLocation

Note: if “Exception in thread "main" org.apache.hadoop.mapred.FileAlreadyExistsException...” occurs, you need to delete the output folder from your Blob. You can do this at portal.azure.com.


The output will be located in the wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput.


If there are multiple output files, merge the files in this directory using the following command:

 

hdfs dfs -cat wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput/* > small.out


Command format: hdfs dfs -cat location/* >outputFile


Then you may exit to your local machine using the command:


exit


You can download the merged file to the local machine (this can be done either from https://portal.azure.com/ or by using the scp command from the local machine). Here is the scp command for downloading this output file to your local machine:


scp <ssh-username>@<cluster-name>-ssh.azurehdinsight.net:/home/<ssh-username>/small.out .


Using the above command from your local machine will download the small.out file into the current directory. Repeat this process for large.tsv. Make sure your output file has exactly two columns of values as shown above.




-1        2

0        1

2        1

