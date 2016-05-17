/*
 * Copyright (c) 2016 Google Inc. All Right Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.cloud.tools.maven;


import com.google.cloud.tools.app.api.devserver.AppEngineDevServer;
import com.google.cloud.tools.app.api.devserver.RunConfiguration;
import com.google.cloud.tools.app.impl.cloudsdk.CloudSdkAppEngineDevServer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * Run App Engine Development App Server synchronously.
 */
@Mojo(name = "run")
@Execute(phase = LifecyclePhase.PACKAGE)
public class RunMojo extends CloudSdkMojo implements RunConfiguration {

  // TODO: the name doesn't fit when you pass a build dir, maybe yamlOrWarPath?
  /**
   * Path to a yaml file, or a directory containing yaml files, or a directory containing
   * WEB-INF/web.xml.
   */
  @Parameter(alias = "devserver.appYamls", property = "app.devserver.appYamls",
      defaultValue = "${project.build.directory}/${project.build.finalName}",
      required = true)
  private List<File> appYamls;

  /**
   * Host name to which application modules should bind. (default: localhost)
   */
  @Parameter(alias = "devserver.host", property = "app.devserver.host")
  private String host;

  /**
   * Lowest port to which application modules should bind. (default: 8080)
   */
  @Parameter(alias = "devserver.port", property = "app.devserver.port")
  private Integer port;

  /**
   * Host name to which the admin server should bind. (default: localhost)
   */
  @Parameter(alias = "devserver.adminHost", property = "app.devserver.adminHost")
  private String adminHost;

  /**
   * Port to which the admin server should bind. (default: 8000)
   */
  @Parameter(alias = "devserver.adminPort", property = "app.devserver.adminPort")
  private Integer adminPort;

  /**
   * Name of the authorization domain to use. (default: gmail.com)
   */
  @Parameter(alias = "devserver.authDomain", property = "app.devserver.authDomain")
  private String authDomain;

  /**
   * Path to the data (datastore, blobstore, etc.) associated with the application. (default: None)
   */
  @Parameter(alias = "devserver.storagePath", property = "app.devserver.storagePath")
  private String storagePath;

  /**
   * The log level below which logging messages generated by application code will not be displayed
   * on the console. Options: debug, info, warning, critical, error. (default: info)
   */
  @Parameter(alias = "devserver.logLevel", property = "app.devserver.logLevel")
  private String logLevel;

  /**
   * The maximum number of runtime instances that can be started for a particular module - the value
   * can be an integer, in what case all modules are limited to that number of instances or a
   * comma-seperated list of module:max_instances e.g. "default:5,backend:3".o (default: None)
   */
  @Parameter(alias = "devserver.maxModuleInstances", property = "app.devserver.maxModuleInstances")
  private Integer maxModuleInstances;

  /**
   * Use mtime polling for detecting source code changes - useful if modifying code from a remote
   * machine using a distributed file system. (default: False)
   */
  @Parameter(alias = "devserver.useMtimeFileWatcher",
      property = "app.devserver.useMtimeFileWatcher")
  private Boolean useMtimeFileWatcher;

  /**
   * Override the application's threadsafe configuration - the value can be a boolean, in which case
   * all modules threadsafe setting will be overridden or a comma- separated list of
   * module:threadsafe_override e.g. "default:False,backend:True". (default: None)
   */
  @Parameter(alias = "devserver.threadsafeOverride", property = "app.devserver.threadsafeOverride")
  private String threadsafeOverride;

  /**
   * The script to run at the startup of new Python runtime instances (useful for tools such as
   * debuggers. (default: None)
   */
  @Parameter(alias = "devserver.pythonStartupScript",
      property = "app.devserver.pythonStartupScript")
  private String pythonStartupScript;

  /**
   * The arguments made available to the script specified in devserver.pythonStartupScript.
   * (default: None)
   */
  @Parameter(alias = "devserver.pythonStartupArgs", property = "app.devserver.pythonStartupArgs")
  private String pythonStartupArgs;

  /**
   * Additional arguments to pass to the java command when launching an instance of the app. May be
   * specified more than once. Example: "-Xmx1024m -Xms256m" (default: None)
   */
  @Parameter(alias = "devserver.jvmFlags", property = "app.devserver.jvmFlags")
  private List<String> jvmFlags;

  /**
   * For custom VM Runtime, specify an entrypoint for custom runtime modules. This is required when
   * such modules are present. Include "{port}" in the string (without quotes) to pass the port
   * number in as an argument. For instance: "gunicorn -b localhost:{port} mymodule:application".
   * (default: )
   */
  @Parameter(alias = "devserver.customEntrypoint", property = "app.devserver.customEntrypoint")
  private String customEntrypoint;

  /**
   * Specify the default runtimes you would like to use. Valid runtimes for Java are ['java',
   * 'custom', 'java7']. (default: )
   */
  @Parameter(alias = "devserver.runtime", property = "app.devserver.runtime")
  private String runtime;

  /**
   * Make files specified in the app.yaml "skip_files" or "static" handles readable by the
   * application. (default: False)
   */
  @Parameter(alias = "devserver.allowSkippedFiles", property = "app.devserver.allowSkippedFiles")
  private Boolean allowSkippedFiles;

  /**
   * Port to which the server for API calls should bind. (default: 0)
   */
  @Parameter(alias = "devserver.apiPort", property = "app.devserver.apiPort")
  private Integer apiPort;

  /**
   * Restart instances automatically when files relevant to their module are changed. (default:
   * True)
   */
  @Parameter(alias = "devserver.automaticRestart", property = "app.devserver.automaticRestart")
  private Boolean automaticRestart;

  /**
   * The log level below which logging messages generated by the development server will not be
   * displayed on the console (this flag is more useful for diagnosing problems in dev_appserver.py
   * rather than in application code). {debug,info,warning,critical,error}. (default: info)
   */
  @Parameter(alias = "devserver.devAppserverLogLevel",
      property = "app.devserver.devAppserverLogLevel")
  private String devAppserverLogLevel;

  /**
   * Skip checking for SDK updates. (if false, use .appcfg_nag to decide) (default: False)
   */
  @Parameter(alias = "devserver.skipSdkUpdateCheck", property = "app.devserver.skipSdkUpdateCheck")
  private Boolean skipSdkUpdateCheck;

  /**
   * Default Google Cloud Storage bucket name. (default: None)
   */
  @Parameter(alias = "devserver.defaultGcsBucketName",
      property = "app.devserver.defaultGcsBucketName")
  protected String defaultGcsBucketName;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    AppEngineDevServer devServer = new CloudSdkAppEngineDevServer(getCloudSdk());

    devServer.run(this);
  }

  @Override
  public List<File> getAppYamls() {
    return appYamls;
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public Integer getPort() {
    return port;
  }

  @Override
  public String getAdminHost() {
    return adminHost;
  }

  @Override
  public Integer getAdminPort() {
    return adminPort;
  }

  @Override
  public String getAuthDomain() {
    return authDomain;
  }

  @Override
  public String getStoragePath() {
    return storagePath;
  }

  @Override
  public String getLogLevel() {
    return logLevel;
  }

  @Override
  public Integer getMaxModuleInstances() {
    return maxModuleInstances;
  }

  @Override
  public Boolean getUseMtimeFileWatcher() {
    return useMtimeFileWatcher;
  }

  @Override
  public String getThreadsafeOverride() {
    return threadsafeOverride;
  }

  @Override
  public String getPythonStartupScript() {
    return pythonStartupScript;
  }

  @Override
  public String getPythonStartupArgs() {
    return pythonStartupArgs;
  }

  @Override
  public List<String> getJvmFlags() {
    return jvmFlags;
  }

  @Override
  public String getCustomEntrypoint() {
    return customEntrypoint;
  }

  @Override
  public String getRuntime() {
    return runtime;
  }

  @Override
  public Boolean getAllowSkippedFiles() {
    return allowSkippedFiles;
  }

  @Override
  public Integer getApiPort() {
    return apiPort;
  }

  @Override
  public Boolean getAutomaticRestart() {
    return automaticRestart;
  }

  @Override
  public String getDevAppserverLogLevel() {
    return devAppserverLogLevel;
  }

  @Override
  public Boolean getSkipSdkUpdateCheck() {
    return skipSdkUpdateCheck;
  }

  @Override
  public String getDefaultGcsBucketName() {
    return defaultGcsBucketName;
  }
}
