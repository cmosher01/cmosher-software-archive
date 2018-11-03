FROM centos

USER root
ENV HOME /root
WORKDIR $HOME

RUN yum install -y rpm-build redhat-rpm-config tree

ENV build $HOME/rpmbuild



RUN mkdir -p $build/{BUILD,RPMS,SOURCES,SPECS,SRPMS}
RUN echo '%_topdir %(echo $HOME)/rpmbuild' > ~/.rpmmacros

COPY foo $build/SOURCES/
RUN chown root: $build/SOURCES/foo

ENV spec $build/SPECS/testing.spec
COPY testing.spec $spec

RUN rpmbuild -ba --define 'VERS 1.0' $spec
RUN rpmbuild -ba --define 'VERS 1.1' $spec
RUN rpmbuild -ba --define 'VERS 1.2' $spec
