package com.sportsant.saas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
            <html>
                <head>
                    <title>Sports Ant SaaS Backend</title>
                    <style>
                        body { font-family: sans-serif; text-align: center; padding: 50px; background-color: #f4f6f9; }
                        .container { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); display: inline-block; }
                        h1 { color: #333; }
                        p { color: #666; font-size: 1.1em; }
                        a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #409eff; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                        a:hover { background-color: #66b1ff; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Sports Ant SaaS Backend API</h1>
                        <p>This is the backend service. It provides REST APIs for the application.</p>
                        <p>To access the user interface, please visit the Frontend Application.</p>
                        <a href="http://localhost:5174">Go to Frontend (Web UI)</a>
                    </div>
                </body>
            </html>
            """;
    }
}
