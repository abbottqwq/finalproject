import React from "react";
import ReactDOM from "react-dom/client";
import "./css/index.css";
import ResultPage from "./page/Result";
import WordCloudPage from "./page/WordCloudPage";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
	<React.StrictMode>
		<WordCloudPage />
		<br />
		<ResultPage />
	</React.StrictMode>
);
