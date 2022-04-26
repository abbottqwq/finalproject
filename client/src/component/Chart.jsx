import React from "react";
import { RadialChart } from "react-vis";
import "../css/chart.css";
export function Chart({ data }) {
	return (
		<div>
			<RadialChart data={data} width={300} height={300} />
		</div>
	);
}
