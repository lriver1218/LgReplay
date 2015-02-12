package com.lge.lgreplaymaker.parser;

import com.lge.lgreplaymaker.event.*;
import java.io.*;

import java.lang.System;
import java.lang.String;
import java.lang.Object;
import java.util.*;

public interface EventParser
{
    public Event parse(String logLine);
}


