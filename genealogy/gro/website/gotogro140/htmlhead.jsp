<%
    String agent = request.getHeader("User-Agent");
    boolean std = (agent != null && agent.indexOf("MSIE") == -1);
    if (std)
    {
        response.setContentType("application/xhtml+xml");
        %><?xml version="1.0" encoding="UTF-8" standalone="no"?>
<%
    }
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<%@ page session="false"%>
	