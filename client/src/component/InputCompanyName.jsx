import React from "react";
import { AutoComplete } from "antd";
const { Option } = AutoComplete;
export function InputCompanyName({ companyNames, setCompanyName }) {
	return (
		<AutoComplete
			style={{
				width: 200,
			}}
			placeholder="input company name here"
			options={companyNames}
			onChange={setCompanyName}
			filterOption={(inputValue, option) =>
				option.value.toUpperCase().indexOf(inputValue.toUpperCase()) !== -1
			}
		></AutoComplete>
	);
}
