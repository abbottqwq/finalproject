import React, { useEffect } from "react";
import { DatePicker, Space } from "antd";
import moment from "moment";

export function InputDate({ setMoments, start, to }) {
	const { RangePicker } = DatePicker;
	const dateFormat = "YYYY-MM-DD";
	// useEffect(() => {
	// 	setMoments([moment(start, dateFormat), moment(to, dateFormat)]);
	// }, []);
	return (
		<div>
			<Space direction="vertical" size={12}>
				<RangePicker
					// defaultValue={[moment(start, "YYYY-MM"), moment(to, "YYYY-MM")]}
					defaultPickerValue={[
						moment(start, dateFormat),
						moment(to, dateFormat),
					]}
					picker="month"
					onChange={setMoments}
					format="YYYY-MM"
				/>
			</Space>
		</div>
	);
}
