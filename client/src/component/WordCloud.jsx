import react from "react";
import "../css/chart.css";
import ReactWordcloud from "react-wordcloud";

export function WordCloud(data) {
	const options = {
		enableTooltip: true,
		deterministic: true,
		fontFamily: "impact",
		fontSizes: [5, 60],
		fontStyle: "normal",
		fontWeight: "normal",
		padding: 1,
		rotations: 3,
		rotationAngles: [0, 90],
		scale: "sqrt",
		spiral: "archimedean",
		transitionDuration: 1000,
	};
	return (
		<div style={{ height: 400, width: 600 }}>
			<ReactWordcloud words={data.data} options={options} />
		</div>
	);
}
