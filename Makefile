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
	@ printf -- "dcsh before ifs: '%s'\n" "$${dcsh}"; \
	if [ -d "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)" ]; then \
		printf -- "DEPENDENCY_CHECK_SUPPRESSIONS_HOME is a dir at: '%s'\n" "$(DEPENDENCY_CHECK_SUPPRESSIONS_HOME)"; \
		dcsh="$${DEPENDENCY_CHECK_SUPPRESSIONS_HOME}"; \
	fi; \
	printf -- "dcsh between ifs: '%s'\n" "$${dcsh}"; \
	if [ ! -d "$${dcsh}" ]; then \
		printf -- "dcsh still not set...\n"; \
		if [ -d "./target/dependency-check-suppressions" ]; then \
			printf -- "dcsh already checked out\n"; \
			dcsh="./target/dependency-check-suppressions" && \
			export dcsh; \
		else \
			printf -- "dcsh: going to check it out\n"; \
			mkdir -p "./target"; \
			git clone $(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_URL) "target/dependency-check-suppressions" && \
				dcsh="./target/dependency-check-suppressions" && \
				export dcsh; \
			if [ -d ./target/dependency-check-suppressions ] && [ -n "$(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_BRANCH)" ]; then \
				cd ./target/dependency-check-suppressions; \
				git checkout $(DEPENDENCY_CHECK_SUPPRESSIONS_REPO_BRANCH); \
				git branch ; \
				cd -; \
			fi; \
			printf "dcsh='%s'\n'" "$${dcsh}"; \
		fi \
	fi; \
	printf -- "dcsh at the end: '%s'\n" "$${dcsh}"; \
	suppressions_path="$${dcsh}/suppressions/$(dependency_check_base_suppressions)"; \
	printf -- "suppressions_path: '%s'\n" "$${suppressions_path}"; \
	cp -av "$${suppressions_path}" $(suppressions_file); \
	mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=$(dependency_check_minimum_cvss) -DassemblyAnalyzerEnabled=$(dependency_check_assembly_analyzer_enabled) -DsuppressionFiles=$(suppressions_file) -DnvdValidForHours=$(dependency_check_nvd_valid_for_hours)

.PHONY: security-check
security-check: dependency-check
