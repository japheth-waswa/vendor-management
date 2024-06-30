#!/bin/bash

if [ ! -f /tmp/conf/nginx.crt ]; then
    openssl req -newkey rsa:2048 -days 365 -nodes -x509 -config /tmp/conf/tls.conf -extensions v3_ca -keyout /tmp/conf/nginx.key -out /tmp/conf/nginx.crt
fi