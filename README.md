<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__html"><p><strong>trackAPI</strong></p>
<p>Restfull api for managing IT projects. Account, password and session entryption.<br>
Ability to add project, create linked tasks and issues. Easy way for sharing projects with clients to look.</p>
<p><strong>Core functionality:</strong></p>
<ul>
<li>easy deployable ( simple docker container with mysql and spring, prepared .sh files for creating and deploying just by one simple command )</li>
<li>user and data protection ( any application that wants to connect to api need to have app token created by the admin. Every user login creates one session token which lifetime is only 15 minutes )</li>
<li>easy documentation and simple endpoints ( unified server requests and easly parsable answers )</li>
</ul>
<p><strong>Main features</strong></p>
<ul>
<li>Creating projects, connecting issues and tasks to projects.</li>
<li>Creating boards using list of elements.</li>
<li>Easy sharing projects with connected items with users.</li>
<li>Easy maintaining stats and progress by expanded element status.</li>
</ul>
<p><strong>How to deploy</strong><br>
Dowload latest release. Copy all files to linux server. Simply make docker_create.sh and docker_run_database.sh executable. Run both commands. Then chmod run.sh and execute it. Your api is running!</p>
    <p><strong> Client app: https://github.com/wjakew/track </strong></p>
</div>
</body>

</html>

