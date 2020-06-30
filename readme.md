# Wake on lan
This service listens to the topic 'computers/+/power'
sending magic packets to computer "+" when receiving the message "on".
Computer information is gathered from the machines properties where data should
be arranged like: name_macAddress_ipAddress. Machines should be comma separated.
