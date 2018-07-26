mvn clean javadoc:javadoc
cd target/site/apidocs/
case "$PWD" in
   */target/site/apidocs) echo "Ok, running at /apidocs";;
   *) echo "Wrong path, leaving." & exit;;
esac

git init
git remote add javadoc https://github.com/fsanti68/simple-state-store.git
git fetch --depth=1 javadoc gh-pages
git add --all
git commit -m "Javadocs updates"
git merge --no-edit -s ours remotes/javadoc/gh-pages
git push javadoc master:gh-pages

echo *** Done! ***
cd ../../..
