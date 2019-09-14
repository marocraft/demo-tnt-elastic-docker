# Demo-tnt-elastic-docker
This is a quick tutirial to up and run with Track N Trace framework, it describes a demonstration of how to link your app that is using TNT with elastic stack (filebeat, logstash,elasticsearch and kibana)

In this tutorial i will show you how to set up the whole elastic stack with a spring boot app that include Track N Trace dependency in order to provide a final vison of this product.
To dig into it here is some few steps :

## Requirements
- Install docker on your system.
- Config System for mac users : https://docs.docker.com/docker-for-mac/install/
- Config System for windows users: https://docs.docker.com/docker-for-windows/install/
- Install Docker Compose on your docker machine https://docs.docker.com/compose/

Notice that you can use virtual box on windows or whatever you want as long as you respet requirement above.

## Step1
- Clone this repository on your system folders. It contains a simple java spring boot demo app and some config files.
## Step2
configure filebeats.yml

Enable input configuration and give exactly the same logs path

```yml
- type: log

  # Change to true to enable this input configuration.
  enabled: true

  # Paths that should be crawled and fetched. Glob based paths.
  paths:
    - data/logs/*
```

Enable the output to send logs to logstash :
```yml
 output.logstash:
  # The Logstash hosts
  hosts: ["logstash:5044"]
```
### Step3
configure logstash.conf

```json
input {
  beats {
    port => 5044
  }
}

filter {
 json {
		source => "message"
      }
}

output {
	elasticsearch {
		hosts => "192.168.99.100:9200"
		index => "tnt-logs"
	}

	stdout { codec => rubydebug }

}
```

- The input section is to define the port where filebeat is using to send logs to logstash
- The Filter section is using json format so no pattern needed be cause Track N Trace uses by default json format to logs
- Then finally we definde elasticsearch as output of logstash, for that you should give two parameters :
 - hosts: your docker host machine , you can get it by using the following command line : docker-machine ip
 - index: the index you want to use in elasticsearch and kibana
the stdout is optional if you need to display the logstash output on your command prompt



