#!/bin/bash

while [ $# -gt 0 ]; do
  case "$1" in
    --stack)
      stack_name="$2"
      ;;
    --bucket)
      bucket="$2"
      ;;
    --db-name)
      db_name="$2"
      ;;
    --db-user)
      db_user="$2"
      ;;
    --git-user)
      git_user="$2"
      ;;
    --git-pass)
      git_pass="$2"
      ;;
    --git-repo)
      git_repo="$2"
      ;;
    --git-folders)
      git_folders="$2"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
  shift
done

echo "Downloading latest flyway-lambda"
if [ ! -e  flyway-all.jar ]; then
  wget https://github.com/Geekoosh/flyway-lambda/releases/latest/download/flyway-all.jar
fi

echo "Packging CloudFormation templates towards deploy"
aws cloudformation package --template-file ./stack.yaml --output-template-file ./stack-out.yaml --s3-bucket $bucket

echo "Deploying CloudFormation templates"
aws cloudformation deploy --template-file ./stack-out.yaml --stack-name $stack_name \
  --parameter-overrides DBName=$db_name Username=$db_user GitUser=$git_user GitPassword=$git_pass GitRepo=$git_repo GitFolders=$git_folders \
  --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
