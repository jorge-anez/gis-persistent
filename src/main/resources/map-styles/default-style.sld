<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor version="1.0.0"
                           xmlns:sld="http://www.opengis.net/sld"
                           xmlns:ogc="http://www.opengis.net/ogc"
                           xmlns:gml="http://www.opengis.net/gml"
                           xmlns:xlink="http://www.w3.org/1999/xlink"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"
>

    <sld:NamedLayer>
        <sld:Name>style-template</sld:Name>
        <sld:UserStyle>
            <sld:Name>layerStyle</sld:Name>
            <sld:IsDefault>1</sld:IsDefault>

            <sld:FeatureTypeStyle>
                <Rule>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>geometryType</ogc:PropertyName>
                            <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>circle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">${Point.fill}</CssParameter>
                                    <CssParameter name="fill-opacity">${Point.fill-opacity}</CssParameter>
                                </Fill>
                                <Stroke>
                                    <CssParameter name="stroke">${Point.stroke}</CssParameter>
                                    <CssParameter name="stroke-width">${Point.stroke-width}</CssParameter>
                                </Stroke>

                            </Mark>
                            <Size>6</Size>
                        </Graphic>
                    </PointSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>${Point.label}</ogc:PropertyName>
                        </Label>
                        <Fill>
                            <CssParameter name="fill">#000000</CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>Line</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>LineString</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">${Line.stroke}</CssParameter>
                            <CssParameter name="stroke-width">${Line.stroke-width}</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>${Line.label}</ogc:PropertyName>
                        </Label>
                        <LabelPlacement>
                            <LinePlacement />
                        </LabelPlacement>
                        <Fill>
                            <CssParameter name="fill">#000000</CssParameter>
                        </Fill>
                    </TextSymbolizer>
                </Rule>
                <Rule>
                    <ogc:Filter>
                        <ogc:Or>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>Polygon</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                            <ogc:PropertyIsEqualTo>
                                <ogc:PropertyName>geometryType</ogc:PropertyName>
                                <ogc:Literal>MultiPolygon</ogc:Literal>
                            </ogc:PropertyIsEqualTo>
                        </ogc:Or>
                    </ogc:Filter>
                    <PolygonSymbolizer>
                        <Fill>
                            <CssParameter name="fill">${Polygon.fill}</CssParameter>
                            <CssParameter name="fill-opacity">${Polygon.fill-opacity}</CssParameter>
                        </Fill>
                        <Stroke>
                            <CssParameter name="stroke">${Polygon.stroke}</CssParameter>
                            <CssParameter name="stroke-width">${Polygon.stroke-width}</CssParameter>
                        </Stroke>
                    </PolygonSymbolizer>
                    <TextSymbolizer>
                        <Label>
                            <ogc:PropertyName>${Polygon.label}</ogc:PropertyName>
                        </Label>
                    </TextSymbolizer>
                </Rule>

            </sld:FeatureTypeStyle>

        </sld:UserStyle>
    </sld:NamedLayer>

</sld:StyledLayerDescriptor>
