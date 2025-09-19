#!/bin/bash

# Create application directory
sudo mkdir -p /opt/bookstore
cd /opt/bookstore

# Copy application files
sudo cp ~/bookstore.jar .

# Create service file
sudo tee /etc/systemd/system/bookstore.service <<EOF
[Unit]
Description=Bookstore Spring Boot Application
After=syslog.target network.target

[Service]
User=root
ExecStart=/usr/bin/java -jar /opt/bookstore/bookstore.jar
SuccessExitStatus=143
Restart=always
RestartSec=5
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=bookstore

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd and start service
sudo systemctl daemon-reload
sudo systemctl enable bookstore
sudo systemctl start bookstore

# Show status
sudo systemctl status bookstore
