import { Slider, InputNumber, Row, Col } from "antd";
import React, { useState, useEffect, useMemo, useCallback } from "react";
import _ from "lodash";

export function InputScaler({
	setOffset,
	setLimit,
	defaultOffset = 0,
	defaultLimit = 100,
	offsetRange = [0, 100],
	limitRange = [0, 100],
	wait = 200,
}) {
	const [_offset, _setOffset] = useState(defaultOffset ? defaultOffset : 0);
	const [_limit, _setLimit] = useState(defaultLimit ? defaultLimit : 20);

	const setVal = (_offset, _limit) => {
		setOffset(_offset);
		setLimit(_limit);
	};

	const debouncedSetVal = useCallback(
		_.debounce((_offset, _limit) => {
			setVal(_offset, _limit);
		}, wait),
		[]
	);

	useEffect(() => {
		debouncedSetVal(_offset, _limit);
	}, [_limit, _offset]);

	const onChangeOffset = (value) => {
		_setOffset(value);
	};

	const onChangeLimit = (value) => {
		_setLimit(value);
	};

	return (
		<>
			<Row>
				<Col>offset:</Col>
				<Col span={6}>
					<Slider
						min={offsetRange[0]}
						max={offsetRange[1]}
						onChange={onChangeOffset}
						value={typeof _offset === "number" ? _offset : 0}
					/>
				</Col>
				<Col span={4}>
					<InputNumber
						min={offsetRange[0]}
						max={offsetRange[1]}
						style={{ margin: "0 16px" }}
						value={_offset}
						onChange={onChangeOffset}
					/>
				</Col>
			</Row>

			<Row>
				<Col>limit:</Col>
				<Col span={6}>
					<Slider
						min={limitRange[0]}
						max={limitRange[1]}
						onChange={onChangeLimit}
						value={typeof _limit === "number" ? _limit : 0}
					/>
				</Col>
				<Col span={4}>
					<InputNumber
						min={limitRange[0]}
						max={limitRange[1]}
						style={{ margin: "0 16px" }}
						value={_limit}
						onChange={onChangeLimit}
					/>
				</Col>
			</Row>
		</>
	);
}
