import React from "react";
import ReactDOM from "react-dom/client";
import "./css/index.css";
import ResultPage from "./page/Result";
import WordCloudPage from "./page/WordCloudPage";
import { InitButton } from "./component/InitButton";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
	<React.StrictMode>
		<InitButton />
		<WordCloudPage />
		<br />
		<ResultPage />
	</React.StrictMode>
);
