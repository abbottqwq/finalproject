import React from "react";
import { Button } from "antd";

export function SubmitButton({ onClick, loading }) {
	return (
		<Button type="primary" loading={loading} onClick={onClick}>
			Submit
		</Button>
	);
}
