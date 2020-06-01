<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="article-id" data-th-content="${article.id}"/>
    <title>[[${article.title}]]</title>
</head>
<body>
<h1 data-th-text="${article.title}"></h1>
<section id="text" data-th-text="${article.text}"></section>
</body>
</html>
