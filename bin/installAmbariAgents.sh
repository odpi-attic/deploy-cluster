# This script installs and starts Ambari 1.7.0 from the public repo.
# Run with the root user.
# Created by Raj Desai (rddesai@us.ibm.com).

IFS=','
NODE_LIST=$1
AMBARI_REPO=$2
AMBARI_HOST=`hostname -f`
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source "$DIR/sh2ju.sh"

#if [[ $EUID -ne 0 ]]; then
#   echo "This script must be run as root"
#   exit 1
#fi

usage(){
        echo "Usage: $0 <comma separated node list> <ambari repo url>"
        exit 1
}

# invoke  usage
# call usage() function if filename not supplied
[[ $# -eq 0 ]] && usage

juLogClean

for _Node in $NODE_LIST; do

juLog -name=DownloadRepo sudo ssh $_Node wget -N $AMBARI_REPO -O /etc/yum.repos.d/ambari.repo
if [ "$?" -ne "0" ]; then
    echo "FAILED to download Ambari repo!"
    exit 1
fi
echo
echo "Installing Ambari Agent on $_Node..."
sudo ssh $_Node yum clean all
juLog -name=InstallAgent sudo ssh $_Node yum -y install ambari-agent
if [ "$?" -ne "0" ]; then
    echo "FAILED to install Ambari Agent!"
    exit 1
fi

echo
echo "Setting Ambari Server in ambari-agent.ini on $_Node."

sudo ssh $_Node sed -i "/hostname=/c\hostname=$AMBARI_HOST" /etc/ambari-agent/conf/ambari-agent.ini
sudo ssh $_Node cat /etc/ambari-agent/conf/ambari-agent.ini | grep hostname=

echo
echo "Starting Ambari Agent on $_Node..."
juLog -name=StartAgent sudo ssh $_Node ambari-agent restart
if [ "$?" -ne "0" ]; then
  echo "FAILED to start Ambari Agent!"
  exit 1
fi

done

echo
echo "Confirm the Agent hosts are registered with the Server."
echo "http://$AMBARI_HOST:8080/api/v1/hosts"
