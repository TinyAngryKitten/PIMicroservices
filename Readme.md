# Triggers actions or homey flows
topic: "action/**action name**"

#Publishes and updates state variables on request

###get state variable by name:
topic: state/get

payload: name of state variable

### update state variable (only boolean implemented as of now)
topic: state/update/**type**/**variable name**
payload: new variable value

### Updates to state variables are also published
topic: state/status/**variable name**

payload: the new value

# Requires a homey bearer token to function
A http server listens for a bearer token in the request body from homey on any path at port 6669.