#!/bin/sh

cp sysctl.conf /etc
sysctl -p

firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --permanent --add-port=10000-41999/tcp
firewall-cmd --reload

systemctl disable firewalld
systemctl stop firewalld
setenforce 0

hostnamectl set-hostname $1
