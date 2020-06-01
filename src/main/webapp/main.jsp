<jsp:useBean id="article" scope="request" type="com.sun.org.apache.xml.internal.security.signature.SignatureProperty"/>
<jsp:useBean id="articles" scope="request" type=""/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>All Posts</title>
</head>
<body>
<h1>Posts</h1>

<ul>
    <li data-th-each="article : ${articles}" class="article">
        <a data-th-href="@{/{articleId}(articleId=${article.id})}"
           data-th-text="${article.title}"></a>
    </li>
</ul>
</body>
</html>
