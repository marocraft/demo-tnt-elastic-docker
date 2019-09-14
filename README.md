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
  - the stdout is optional if you need to display the logstash output on your command prompt

## Step 4
Configure docker-compose.yml

here is the whole configuration:

```yml
#docker-compose
version: "3.7"
services: 
  demo-tnt:
    image: housseinetassa/demotnt
    volumes:
      - //c/dockertoolbox/dockerCompose/logs:/usr/share/filebeat/data/logs
    environment:
      - "JAVA_OPTS=-DLOGPATH=/usr/share/filebeat/data/logs/logs.txt"
    ports:
      - 8080:8080
    depends_on:
      - filebeat
    networks:
      - logging-network
  elasticsearch:
    image: elasticsearch:7.3.1
    ports:
      - "9200:9200"
      - "9300:9300"
    environment: 
      - discovery.type=single-node
      - xpack.security.enabled=false
    networks: 
      - logging-network
  kibana:
    image: kibana:7.3.1
    depends_on:
      - elasticsearch
    ports: 
      - 5601:5601
    networks: 
      - logging-network
  logstash:
    image: logstash:7.3.1
    depends_on: 
      - kibana
    volumes:
      - //c/dockertoolbox/dockerCompose/logstash.conf:/usr/share/logstash/pipeline/logstash.conf:ro 
    networks: 
      - logging-network
  filebeat:
    image: docker.elastic.co/beats/filebeat:7.3.1
    command: filebeat -e -strict.perms=false
    user: root
    configs:
      - source: //c/dockertoolbox/dockerCompose/filebeat.yml
        target: /usr/share/filebeat/filebeat.yml
    volumes:
      - //c/dockertoolbox/dockerCompose/filebeat.yml:/usr/share/filebeat/filebeat.yml
      - //c/dockertoolbox/dockerCompose/logs:/usr/share/filebeat/data/logs
      - /var/run/docker.sock:/var/run/docker.sock
      - /var/lib/docker/containers/:/var/lib/docker/containers/:ro
    networks: 
      - logging-network
    depends_on:
      - elasticsearch
    restart: unless-stopped
networks: 
  logging-network:
    driver: bridge
```
You need to define each service configuration, make sure to change paths with your own folder path , and if you are using docker on windows you may need to mount volumes if they are not accessible by docker-machine: https://medium.com/@Charles_Stover/fixing-volumes-in-docker-toolbox-4ad5ace0e572

## step 5

In dockerCompose folder run the following command docker-compose up
this will run all services using docker-compose.yml

finally enjoy playing with your logs in kibana!

To shut it down use : docker-compose down


---
Author: Housseine Tassa
