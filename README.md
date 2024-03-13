# DataLeakage_Fall2023

![Build](https://github.com/cd721/data_leakage_plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
First version of a plugin that detects data leakage in Python code for ML.
<!-- Plugin description end -->

[//]: # (This Fancy IntelliJ Platform Plugin is going to be your implementation of the brilliant ideas that you have.)

[//]: # ()
[//]: # (This specific section is a source for the [plugin.xml]&#40;/src/main/resources/META-INF/plugin.xml&#41; file which will be extracted by the [Gradle]&#40;/build.gradle.kts&#41; during the build process.)

[//]: # ()
[//]: # (To keep everything working, do not remove `<!-- ... -->` sections.)

## Installation

- **<ins>Prerequisite:</ins>**
  Please have Docker **INSTALLED** and **RUNNING BEFORE** you run the Data Leakage Plugin. Links to the documents explaining how to install Docker and videos down below. 

  **<ins>Documents:</ins>**
  https://docs.google.com/document/d/1deSKzRfFrcQvi1nLRVJI5ASzi7WKHJ3PlrdtxZf3bbI/edit?usp=sharing

  **<ins>Installing Docker on Mac Video (first half):</ins>**
  https://www.youtube.com/watch?v=3fiYHaaz5eQ

  **<ins>Installing Docker on Windows 11 Video:</ins>**
  https://www.youtube.com/watch?v=8qQeODFSPQ4
  
- **<ins>Data Leakage Plugin Download:</ins>**
  Please follow the directions on the documents and the videos in order to download the plugin. Instructions provided for both Mac and Windows OS Systems. 

  **<ins>Documents:</ins>**
  https://docs.google.com/document/d/1FFwkdSlUPQfNzlNyBjiKDZxQPmASghxG8njG05N_9lQ/edit?usp=sharing

  **<ins>Installation for Mac (second half of the video):</ins>**
  https://www.youtube.com/watch?v=3fiYHaaz5eQ

  **<ins>Installation for Windows 11 Video:</ins>**
  https://www.youtube.com/watch?v=l-dHJxA3S0c

## Usage

- **<ins>Data Leakage Plugin Usage:</ins>**
  Video down below shows how to run the data leakage plugin for a given python file in pycharm. 
  https://youtu.be/gb9sF-MTsM8


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
>>>>>>> develop
