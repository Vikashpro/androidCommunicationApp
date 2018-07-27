<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require '../vendor/autoload.php';
require '../src/config/db.php';

$settings =  [
    'settings' => [
        'displayErrorDetails' => true,
    ],
];

$app = new \Slim\App($settings);

$app->get('/hello/{name}', function (Request $request, Response $response, array $args) {
    $name = $args['name'];
    $response->getBody()->write("Hello, $name");

    return $response;
});


require_once "../src/routes/notices.php";

require_once "../src/routes/notice_board.php";

require_once "../src/routes/faculties.php";

require_once "../src/routes/login.php";

require_once "../src/routes/searches.php";


require_once "../src/routes/conversation.php";

require_once "../src/routes/results.php";

require_once "../src/routes/public_notice.php";

$app->run();
?>