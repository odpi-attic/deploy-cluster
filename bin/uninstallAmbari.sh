#!/bin/sh

# This script uninstalls Ambari Server, Ambari Agents and removes the repo files.
# Run with the root user.
# Created by Raj Desai (rddesai@us.ibm.com).

#if [[ $EUID -ne 0 ]]; then
#   echo "This script must be run as root"
#   exit 1
#fi

usage(){
        echo "Usage: $0 <comma separated node list>"
        exit 1
}

# invoke  usage
# call usage() function if filename not supplied
[[ $# -eq 0 ]] && usage

NODE_LIST=$1
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source "$DIR/sh2ju.sh"
IFS=','

juLogClean

for _Node in $NODE_LIST; do
    echo "Stopping Ambari agent on $_Node"
    sudo ssh $_Node ambari-agent stop
done

echo "Stopping Ambari server..."
while read p; do
  echo "Attempting to kill process id: $p"
  sudo kill -9 $p
done < /var/run/ambari-server/ambari-server.pid
#PID=`sudo cat /var/run/ambari-server/ambari-server.pid`
#sudo sudo kill -9 $PID
sudo ambari-server reset --silent

for _Node in $NODE_LIST; do
    echo "Removing Ambari Agent on $_Node"
    juLog -name=RemoveLog4j sudo ssh $_Node yum -y erase ambari-log4j
    juLog -name=RemoveAgent sudo ssh $_Node yum -y erase ambari-agent
    juLog -name=RemoveAgentBinaries sudo ssh $_Node rm -rf /var/lib/ambari-agent
    sudo ssh $_Node yum -y erase mysql mysql-server mysql-devel mysql-libs
    sudo ssh $_Node rm -rf /var/lib/mysql/*
    sudo ssh $_Node yum -y erase ambari-metrics*
    sudo ssh $_Node rm -rf /var/run/ambari-metrics-* /var/log/ambari-metrics-* /var/lib/ambari-metrics-* /usr/lib/ambari-metrics-*
    sudo ssh $_Node rm -rf /usr/lib/ams-hbase /etc/ams-hbase /var/run/ams-hbase
done

echo "Removing Ambari server"
sudo yum -y erase ambari-server
if [ "$?" -ne "0" ];then
    PACKAGE=`rpm -qa | grep ambari-server | head -1`
    rpm -e --allmatches $PACKAGE
fi
juLog -name=RemoveServerBinaries sudo rm -rf /var/lib/ambari-server

for _Node in $NODE_LIST; do
    echo "Removing repository files on $_Node"
    sudo ssh $_Node yum clean all
    sudo ssh $_Node rm -rf /etc/yum.repos.d/ambari.repo
    sudo ssh $_Node rm -rf /etc/yum.repos.d/BIGINSIGHTS*.repo
    sudo ssh $_Node rm -rf /etc/yum.repos.d/HDP*.repo
    sudo ssh $_Node rm -rf /var/cache/yum
done
