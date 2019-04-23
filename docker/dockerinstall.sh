sudo yum check-update
curl -fsSL https://get.docker.com/ | sh
sudo systemctl enable docker
sudo systemctl start docker
sudo systemctl status docker
sudo usermod -aG docker $(whoami)
