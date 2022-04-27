import React, { useState } from "react";
import { VictoryPie, VictoryTheme } from "victory";
import _ from "lodash";
export function Chart({ data }) {
	return (
		<div style={{ width: "50%", height: "auto" }}>
			<VictoryPie
				width={450}
				theme={VictoryTheme.material}
				events={[
					{
						target: "data",
						eventHandlers: {
							onClick: () => {
								return [
									{
										target: "labels",
										mutation: (props) => {
											const datum = props.datum;
											return props.text.includes("%")
												? { text: `${datum.x}: ${datum.y}` }
												: { text: `${datum.x}: ${_.round(datum.p * 100, 1)}%` };
										},
									},
								];
							},
						},
					},
				]}
				data={data}
				labels={({ datum }) => `${datum.x}: ${datum.y}`}
			/>
		</div>
	);
}
// `${datum.x}: ${_.round(datum.p * 100, 1)}%`
