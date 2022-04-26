import React from "react";
import { DatePicker, Space } from "antd";

export function InputDate({ setMoments }) {
	const { RangePicker } = DatePicker;
	return (
		<div>
			<Space direction="vertical" size={12}>
				<RangePicker onChange={setMoments} format="MM-DD-YYYY" />
			</Space>
		</div>
	);
}
