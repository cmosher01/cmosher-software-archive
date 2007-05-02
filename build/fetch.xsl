<?xml version="1.0" encoding="UTF-8"?>
<!--
Transforms an Eclipse .classpath file into an
Ant build script that fetches the dependent
projects listed in the .classpath file.

For example:
.classpath file:
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
	<classpathentry kind="src" path="src"/>
	<classpathentry kind="src" path="test"/>
	<classpathentry kind="src" path="testing"/>
	<classpathentry kind="src" path="/uuid"/>
	<classpathentry kind="src" path="/jug-1-1-2"/>
	<classpathentry kind="src" path="/junit-3-8-1"/>
	<classpathentry kind="output" path="bin"/>
</classpath>

gets transformed into:
<?xml version="1.0" encoding="UTF-8"?>
<project>
<target name="fetch">
<fetch xmlns="" repository="${svn.repos.name}" project="uuid" path="${svn.base.path}"/>
<fetch xmlns="" repository="${svn.repos.vendor.name}" project="jug-1-1-2"/>
<fetch xmlns="" repository="${svn.repos.vendor.name}" project="junit-3-8-1"/>
</target>
</project>

which could be built using the following ant script:
<?xml version="1.0" encoding="UTF-8"?>
<project name="fetch" default="default">
    <import file="fetch_deps.xml" />
    <target name="default" depends="fetch" />
</project>
where fetch_deps.xml is the file that contains the transformation.
Note that the ant script must also macrodef the fetch task
-->
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="xml" />
    <template match="text()" />

    <template match="classpath">
        <element name="project" namespace="">
            <attribute name="name">fetch_xslgen</attribute>
            <text>&#x0A;</text>
            <element name="target" namespace="">
                <attribute name="name">fetch</attribute>
                <text>&#x0A;</text>
                <apply-templates />
            </element>
            <text>&#x0A;</text>
        </element>
        <text>&#x0A;</text>
    </template>

    <template match="classpathentry[(@kind='src') and starts-with(@path,'/') and (contains(@path,'-'))]">
        <element name="fetch" namespace="">
            <attribute name="repository">${svn.repos.vendor.name}</attribute>
            <attribute name="project">
                <value-of select="substring(@path,2)" />
            </attribute>
        </element>
        <text>&#x0A;</text>
    </template>

    <template match="classpathentry[(@kind='src') and starts-with(@path,'/') and (not(contains(@path,'-')))]">
        <element name="fetch" namespace="">
            <attribute name="repository">${svn.repos.name}</attribute>
            <attribute name="project">
                <value-of select="substring(@path,2)" />
            </attribute>
            <attribute name="path">${svn.base.path}</attribute>
            <attribute name="rev">${svn.repos.rev}</attribute>
        </element>
        <text>&#x0A;</text>
    </template>
</stylesheet>
