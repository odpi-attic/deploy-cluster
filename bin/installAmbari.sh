#!/bin/sh

# This script installs and starts Ambari 1.7.0 from the public repo.
# Run with the root user.
# Created by Raj Desai (rddesai@us.ibm.com).

#if [[ $EUID -ne 0 ]]; then
#   echo "This script must be run as root"
#   exit 1
#fi

usage(){
        echo "Usage: $0 <ambari repo url>"
        exit 1
}

# invoke  usage
# call usage() function if filename not supplied
[[ $# -eq 0 ]] && usage

AMBARI_REPO=$1
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source "$DIR/sh2ju.sh"

juLogClean

echo
echo "Downloading the Ambari repo..."
juLog -name=DownloadRepo sudo wget -N $AMBARI_REPO -O /etc/yum.repos.d/ambari.repo
if [ "$?" -ne "0" ]; then
        echo "FAILED to download Ambari repo!"
        exit 1
fi
   
echo
echo "Installing Ambari Server..."
yum clean all
juLog -name=InstallAmbari sudo yum -y install ambari-server
if [ "$?" -ne "0" ]; then
    echo "FAILED to install Ambari Server!"
    exit 1
fi

echo
echo "Downloading the Ambari jdk..."
juLog -name=DownloadJDK sudo wget -nv http://public-repo-1.hortonworks.com/ARTIFACTS/jdk-8u40-linux-x64.tar.gz -O /var/lib/ambari-server/resources/jdk-8u40-linux-x64.tar.gz
if [ "$?" -ne "0" ]; then
        echo "FAILED to download Ambari jdk!"
        exit 1
fi

echo
echo "Downloading the Ambari jce..."
juLog -name=DownloadJCE sudo wget -nv http://public-repo-1.hortonworks.com/ARTIFACTS/jce_policy-8.zip -O /var/lib/ambari-server/resources/jce_policy-8.zip
if [ "$?" -ne "0" ]; then
        echo "FAILED to download Ambari jce!"
        exit 1
fi

echo
echo "Setting up Ambari Server..."
juLog -name=SetupAmbari sudo ambari-server setup --silent
if [ "$?" -ne "0" ]; then
  echo "FAILED to setup Ambari Server!"
  exit 1
fi

sudo ambari-server reset --silent

echo
echo "Starting Ambari Server..."
sudo ambari-server start
if [ "$?" -ne "0" ]; then
  echo "FAILED to start Ambari Server!"
  exit 1
fi

echo
echo "Open up a web browser and go to http://<ambari-server-host>:8080."
echo "Log in with username admin and password admin and follow on-screen instructions."
