<?xml version="1.0" encoding="UTF-8"?>
<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="text" />
    <template match="text()" />

    <template match="classpathentry[(@kind='src') and (starts-with(@path,'/')) and (contains(@path,'-'))]">
        <value-of select="substring(@path,2)" />
        <text>&#x0A;</text>
    </template>
</stylesheet>
