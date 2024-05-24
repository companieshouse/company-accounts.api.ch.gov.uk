artifact_name := company-accounts.api.ch.gov.uk
dependency_check_base_suppressions:=base_suppressions_spring_6.xml
dependency_check_minimum_cvss := 4
dependency_check_assembly_analyzer_enabled := false
dependency_check_nvd_valid_for_hours = 172
DEPENDENCY_CHECK_SUPPRESSIONS_REPO_URL:=git@github.com:companieshouse/dependency-check-suppressions.git
DEPENDENCY_CHECK_SUPPRESSIONS_REPO_BRANCH:=feature/suppressions-for-company-accounts-api
suppressions_file := target/suppressions.xml

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-*.zip
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	mvn package -DskipTests=true
	cp ./target/$(artifact_name)-unversioned.jar ./$(artifact_name).jar

.PHONY: test
test: test-unit

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration:
	mvn integration-test verify -Dskip.unit.tests=true failsafe:verify

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./routes.yaml $(tmpdir)
	cp ./start.sh $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: dependency-check
dependency-check:
	@ if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		dcsh="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
	fi; \
	if [ ! -d "$${dcsh}" ]; then \
		if [ -d "./target/dependency-check-suppressions" ]; then \
			dcsh="./target/dependency-check-suppressions"; \
		else \
			mkdir -p "./target"; \
			git clone $(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_URL) "target/dependency-check-suppressions" && \
				dcsh="./target/dependency-check-suppressions"; \
			if [ -d ./target/dependency-check-suppressions ] && [ -n "$(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_BRANCH)" ]; then \
				cd ./target/dependency-check-suppressions; \
				git checkout $(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_BRANCH); \
				git branch ; \
				cd -; \
			fi; \
		fi \
	fi; \
	suppressions_path="$${dcsh}/suppressions/$(dependency_check_base_suppressions)"; \
	cp -av "$${suppressions_path}" $(suppressions_file); \
	mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=$(dependency_check_minimum_cvss) -DassemblyAnalyzerEnabled=$(dependency_check_assembly_analyzer_enabled) -DsuppressionFiles=$(suppressions_file) -DnvdValidForHours=$(dependency_check_nvd_valid_for_hours)

.PHONY: security-check
security-check: dependency-check
