FROM ubuntu
MAINTAINER Christopher A. Mosher <cmosher01@gmail.com>

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get install -y curl
RUN curl https://s3.amazonaws.com/download.fpcomplete.com/ubuntu/fpco.key | apt-key add -
RUN echo "deb http://download.fpcomplete.com/ubuntu/$(lsb_release -sc) stable main" >/etc/apt/sources.list.d/fpco.list
RUN apt-get update && apt-get install -y stack tree

ENV HOME /root
WORKDIR $HOME
