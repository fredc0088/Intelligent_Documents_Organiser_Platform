# Intelligent Documents Organiser Platform

Platform to perform analysis and clustering for documents belonging to a file system.

## Prerequisites

jre 8

javaFX (it's included with Oracle JDK, if OpenJDK is being used please find alternatives such as OpenJFX, see below)

For building: sbt 1.0+

### OpenFX
Usually javafx comes shipped together with Oracle jre/jdk. However, that may not be the case for some distribution of the OpenJDK.
In that case the suggestion is to try [OpenFX](https://openjfx.io/openjfx-docs/#install-javafx).
After installing it following the instruction, include it in the sbt classpath, or move the jar specifically into a `lib` folder in the root of the project.

## Build

sbt compile
sbt run

An executable jar can be created with 'sbt assembly'

## Testing

sbt test

## Use
Please check the [wiki](https://github.com/fredc0088/Intelligent_Documents_Organiser_Platform/wiki).

## Built With

sbt 1.1.4
Scala 2.12.3

## Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners or masters of this repository before making a change. 

### Pull Request Process

1. Ensure any install or build dependencies are removed before the end of the layer when doing a 
   build.
2. Update the README.md with details of changes, this includes new environment 
   variables, exposed ports, useful file locations,etc....
3. Increase the version numbers in any examples files and the README.md to the new version that this
   Pull Request would represent.
4. You may merge the Pull Request in once you have the sign-off of two other developers, or if you 
   do not have permission to do that, you may request the second reviewer to merge it for.

## Versioning

## Current Version

[1.0]

## Changelog

## Authors

Federico Cocco

## License

## Notes

Academic year 2017/2018

Final project for BSC Computing at Birkbeck, University of London

London, UK
