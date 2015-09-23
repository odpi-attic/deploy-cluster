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
proc_owner_list=ambari-qa,hdfs,mapred,mysql,nobody,yarn,zookeeper
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source "$DIR/sh2ju.sh"
IFS=','
#PID=`sudo cat /var/run/ambari-server/ambari-server.pid`

juLogClean

echo "Stopping ambari-server..."
#sudo sudo kill -9 $PID
while read p; do
  echo "Attempting to kill process id: $p"
  sudo kill -9 $p
done < /var/run/ambari-server/ambari-server.pid
sudo ambari-server reset --silent

for _Node in $NODE_LIST; do
    echo "Stopping ambari-agent on $_Node"
    sudo ssh $_Node ambari-agent stop
done



for _Node in $NODE_LIST; do
    echo "Running HostCleanup.py on $_Node"
    sudo ssh $_Node yum clean all
    sudo ssh $_Node rpmdb --rebuilddb
    for _User in $proc_owner_list; do
        echo "Killing all processes owned by $_User on $_Node."
        sudo ssh $_Node pkill -9 -u `id -u $_User`
    done
    sudo scp $DIR/HostCleanup* $_Node:/tmp
    juLog -name=HostCleanup sudo ssh $_Node python /usr/lib/python2.6/site-packages/ambari_agent/HostCleanup.py --silent --skip=users -f /tmp/HostCleanup.ini,/tmp/HostCleanup_Custom_Actions.ini -o /tmp/cleanup-log.file
     
done

# This is a workaround if needed.
#for _Node in $NODE_LIST; do
#    sudo ssh $_Node yum -y --setopt=tsflags=noscripts remove hadoop\*
#done

exit 0
