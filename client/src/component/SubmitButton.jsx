import React from "react";
import { Button } from "antd";

export function SubmitButton({ children, onClick, loading, ...props }) {
	return (
		<Button type="primary" loading={loading} onClick={onClick}>
			{children || "submit"}
		</Button>
	);
}
