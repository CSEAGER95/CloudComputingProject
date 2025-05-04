deploy and containerize frontend with powershell: "C:\Program Files\Git\bin\bash.exe" -c "./deploy-to-cloud-run.sh"

or git ./deploy-to-cloud-run.sh

from /backend: mvn clean package appengine:deploy -DskipTests

both frontend and backend are containerized and uploaded to GCP, Connected to sql database, just need to get the whole thing to work. currently the website isnt displaying any stories and its not clearly receiving anything.