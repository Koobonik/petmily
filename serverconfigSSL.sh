sudo iptables --table nat --append PREROUTING --protocol tcp --dport 80 --jump REDIRECT --to-ports 443
sudo iptables --table nat --append PREROUTING --protocol tcp --dport 443 --jump REDIRECT --to-ports 8080
