#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Homely Test Report Generator ===${NC}"
echo -e "${YELLOW}This script generates test reports for the Homely application${NC}"
echo

# Run tests and generate reports
echo -e "${YELLOW}Running tests and generating reports...${NC}"
mvn clean test
if [ $? -ne 0 ]; then
  echo -e "${RED}Tests failed${NC}"
  exit 1
fi
echo -e "${GREEN}Tests completed successfully${NC}"
echo

# Check if reports were generated
if [ -d "target/surefire-reports" ]; then
  echo -e "${GREEN}Test reports generated in target/surefire-reports/${NC}"
  echo -e "${YELLOW}XML reports:${NC}"
  ls -la target/surefire-reports/*.xml
  echo
  echo -e "${YELLOW}Text reports:${NC}"
  ls -la target/surefire-reports/*.txt
else
  echo -e "${RED}Test reports not found in target/surefire-reports/${NC}"
fi

# Check if JaCoCo reports were generated
if [ -d "target/site/jacoco" ]; then
  echo -e "${GREEN}JaCoCo coverage reports generated in target/site/jacoco/${NC}"
  echo -e "${YELLOW}Open target/site/jacoco/index.html in a browser to view the coverage report${NC}"
else
  echo -e "${RED}JaCoCo coverage reports not found in target/site/jacoco/${NC}"
fi

echo
echo -e "${GREEN}=== Report generation completed ===${NC}"