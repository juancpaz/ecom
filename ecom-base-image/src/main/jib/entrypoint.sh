#!/bin/bash

ac_build_base=false
ac_nop=false
ac_test=false
ac_mainclass=""
ac_command=""
ac_filename=""
ac_error_parse=false
ac_skip_healthcheck=false
ac_healthcheck_result=""
ac_skip_service=false

function parse_args() {
	while [ $# -gt 0 ]; do
		case "$1" in
			"--build-base")
				ac_build_base=true
				shift
				break;
				;;
			"--nop")
				ac_nop=true
				shift
				break;
				;;
			"--test")
				ac_test=true
				shift
				;;
			"--mainclass")
				ac_mainclass="$2"
				shift
				shift
				;;
			"--healthcheck")
				ac_command="healthcheck"
				shift
				;;
			"--skip-healthcheck")
				ac_skip_healthcheck=true
				shift
				;;
			"--skip-service")
				ac_skip_service=true
				shift
				;;
			*)
				if [ "${ac_command}" = "healthcheck" ]; then
					ac_filename="$1"
				else
					echo "Opcion erronea: $1"
					ac_error_parse=true
					break
				fi
				shift
				;;
		esac
	done
}

function build_base() {
	echo "Build base"
}

function exec_healthchecks() {
	echo "=== START HEALTHCHECK ==="
	spring_config="--eureka.client.enabled=false"
	cmd="java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -cp /app/resources/:/app/classes/:/app/libs/*"
	mainClass='com.juancpaz.ecom.tools.CommandLineTool'
	if [ "${ac_filename}" = "" ]; then
		cmd="$cmd $mainClass $spring_config health-check"
	else
		cmd="$cmd $mainClass $spring_config health-check -f $ac_filename"
	fi
	echo "$cmd"
	if [ "${ac_test}" = "false" ]; then
	    ac_healthcheck_result="ERROR"
		$cmd && ac_healthcheck_result="OK"
	fi
	echo "=== END HEALTHCHECK ==="
}

function entry_point() {
	echo "=== START SERVICE ==="
	if [ "${ac_mainclass}" = "" ]; then
		echo "No --mainclass defined"
		return
	fi
	cmd="java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -cp /app/resources/:/app/classes/:/app/libs/*"
	mainClass="${ac_mainclass}"
	cmd="$cmd $mainClass $@"
	echo "$cmd"
	if [ "${ac_test}" = "false" ]; then
		$cmd
	fi
	echo "=== END SERVICE ==="
}

function main() {
	parse_args $@
	if [ "$ac_error_parse" = "true" ]; then
		show_syntax
		return
	fi
	if [ "$ac_build_base" = "true" ]; then
		build_base
	elif [ "$ac_nop" = "true" ]; then
		echo "NOP"
	else
		if [ "${ac_test}" = "true" ]; then
			echo "\${ac_nop}=${ac_nop}"
			echo "\${ac_test}=${ac_test}"
			echo "\${ac_mainclass}=${ac_mainclass}"
			echo "\${ac_command}=${ac_command}"
			echo "\${ac_filename}=${ac_filename}"		
		fi
		if [ "${ac_skip_healthcheck}" = "true" ]; then
			echo "Health Check skipped"
		else
			if [ "${ac_command}" = "healthcheck" ]; then		
				echo "Executing Healthchecks"
				exec_healthchecks
			fi
		fi
		if [ "${ac_skip_service}" = "false" ]; then
			if [ ! "${ac_healthcheck_result}" = "ERROR" ]; then
				entry_point
			fi
		else 
			echo "Skipping Service Execution"
		fi	
	fi
}

function show_syntax() {
	echo "Sintaxis: $0 (--nop | --build-base | --test) | ([--skip-healthcheck] --healthcheck [filename]) "
	return
}

function install_packages() {
	echo -n "Installing pakages... "
	apt-get update > /dev/null 2>&1
	for package in $(cat /packages.txt); do
		apt-get install -y ${package} > /dev/null 2>&1
	done
	echo "Done"
}

install_packages
if [ $# -eq 0 ]; then
	show_syntax
else
	main $@
fi
